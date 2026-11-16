import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_7904f5b05e713be39af557133d718a7c
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return "1".equals(mapRow.get("MODIFY_FLAG"))?"<font color=red>"+mapRow.get("DAY_DL")+"</font>":"mapRow.get("DAY_DL")";
    }
}
