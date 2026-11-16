import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_0d433ae7a171fc424401b8cbc61e52cb
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return StringUtils.isEmpty("DDZY")?"":"<span datavalue='"+mapRow.get("DDZY")+"' onclick='pw_notify_deadnumline_view(this);'>查看</span>";

    }
}
