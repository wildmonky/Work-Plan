import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_c746c3773afd529102bd45d5ce5f2fe2
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:jsCommon.deleteTableData('TRANSFORMER_SUBSTATION','"+mapRow.get("ID")+"');";
return url;

    }
}
